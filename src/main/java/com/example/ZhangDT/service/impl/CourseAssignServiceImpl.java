package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.ClassSchedule;
import com.example.ZhangDT.bean.Class;
import com.example.ZhangDT.bean.exam.Classroom;
import com.example.ZhangDT.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseAssignServiceImpl {

    @Autowired
    private ClassroomMapper classroomMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseOfferingMapper courseOfferingMapper;

    // 染色体：一个班级一门课的安排
    static class Gene {
        int classId;
        int courseId;
        int teacherId;
        int classroomId;
        int timeSlot;
    }

    // 个体：一份完整课表
    static class Chromosome {
        List<Gene> genes;
        int fitness;

        Chromosome(List<Gene> genes) {
            this.genes = genes;
            this.fitness = 0;
        }
    }

    public List<ClassSchedule> assign(List<Integer> majorIds){
        if (majorIds == null || majorIds.isEmpty()) return Collections.emptyList();

        int populationSize = 100;
        int generations = 1000;
        int timeCount = 24; // 假设一天24个时间段
        double mutationRate = 0.1;

        // 局部变量，避免多线程污染
        Map<Integer,List<Integer>> mp1 = new HashMap<>(); // classId -> courseIds
        Map<Integer,List<Integer>> mp2 = new HashMap<>(); // courseId -> teacherIds

        List<Class> classes = new ArrayList<>();
        for(Integer majorId : majorIds){
            List<Class> result = classMapper.findBymajorId(majorId);
            if (result != null) classes.addAll(result);
        }

        List<Classroom> classrooms = classroomMapper.findAll();
        if (classrooms == null || classrooms.isEmpty()) return Collections.emptyList();

        Set<Integer> courseIds = new HashSet<>();
        // 装配classId->courseIds
        for(Class c: classes){
            Integer classId = c.getClassId();
            Integer majorId = c.getMajorId();
            List<Integer> courses = courseOfferingMapper.getCourseIdByMajorId(majorId);
            if (courses == null || courses.isEmpty()) continue;
            courseIds.addAll(courses);
            mp1.put(classId, courses);
        }

        // 装配courseId->teacherIds
        for(Integer courseId : courseIds){
            List<Integer> teacherIds = teacherMapper.getTeacherIdByCourseId(courseId);
            if (teacherIds != null && !teacherIds.isEmpty()) {
                mp2.put(courseId, teacherIds);
            }
        }

        List<Gene> baseGenes = new ArrayList<>();
        for(Class c: classes){
            List<Integer> courseList = mp1.get(c.getClassId());
            if (courseList == null) continue;
            for(Integer courseId : courseList){
                Gene gene = new Gene();
                gene.classId = c.getClassId();
                gene.courseId = courseId;
                baseGenes.add(gene);
            }
        }

        List<Chromosome> population = new ArrayList<>();
        for(int i=0;i<populationSize;i++){
            try {
                population.add(randomChromosome(baseGenes, classrooms, timeCount, mp2));
            } catch (Exception e) {
                // 跳过异常个体
            }
        }

        for(int gen=0;gen<generations;gen++){
            for(Chromosome c: population){
                c.fitness=calcFitness(c);
            }

            population.sort(Comparator.comparingInt(c-> -c.fitness));

            if(population.get(0).fitness==0) break;

            // 新生代
            List<Chromosome> newPop=new ArrayList<>();
            for(int i=0;i<population.size();i++){
                Chromosome parent1 = population.get(i%(population.size()/2));
                Chromosome parent2 = population.get((i+1)%(population.size()/2));
                try {
                    Chromosome child = crossover(parent1,parent2,mp2,classrooms,timeCount);
                    mutate(child,classrooms,timeCount,mutationRate,mp2);
                    newPop.add(child);
                } catch (Exception e) {
                    // 跳过异常个体
                }
            }
            population=newPop;
        }

        Chromosome best = population.get(0);
        List<ClassSchedule> result = new ArrayList<>();
        for (Gene g : best.genes) {
            ClassSchedule cs = new ClassSchedule();
            cs.setClassId(g.classId);
            cs.setCourseId(g.courseId);
            cs.setTeacherId(g.teacherId);
            cs.setClassRoomId(g.classroomId);
            cs.setTimeSlot(g.timeSlot);

            cs.setClassName(classMapper.selectById(g.classId).getClassName());
            cs.setCourseName(courseMapper.selectById(g.courseId).getCourseName());
            cs.setTeacherName(teacherMapper.selectById(g.teacherId).getName());
            cs.setClassRoomName(classroomMapper.selectById(g.classroomId).getClassroomName());

            result.add(cs);
        }
        return result;
    }

    private Chromosome randomChromosome(List<Gene> baseGenes, List<Classroom> classrooms, int timeCount, Map<Integer,List<Integer>> mp2) {
        Random random = new Random();
        List<Gene> genes = new ArrayList<>();
        for(Gene base: baseGenes){
            Gene g = new Gene();
            g.classId = base.classId;
            g.courseId = base.courseId;
            g.timeSlot=random.nextInt(timeCount);
            g.classroomId=classrooms.get(random.nextInt(classrooms.size())).getClassroomId();
            List<Integer> teacherIds = mp2.get(base.courseId);
            if (teacherIds == null || teacherIds.isEmpty()) throw new RuntimeException("No teacher for course " + base.courseId);
            g.teacherId=teacherIds.get(random.nextInt(teacherIds.size()));
            genes.add(g);
        }
        return new Chromosome(genes);
    }

    private int calcFitness(Chromosome c){
        int conflict = 0;
        List<Gene> genes = c.genes;

        // 统计每个时间段每个教室的班级数
        Map<String, Integer> roomTimeCount = new HashMap<>();
        for (Gene g : genes) {
            String key = g.timeSlot + "-" + g.classroomId;
            roomTimeCount.put(key, roomTimeCount.getOrDefault(key, 0) + 1);
        }
        // 对超出2个班级的情况进行惩罚
        for (int count : roomTimeCount.values()) {
            if (count > 2) {
                conflict += (count - 2); // 每超出1个班级，冲突+1
            }
        }

        // 其它冲突（老师、班级时间冲突等）保持不变
        for(int i=0;i<genes.size();i++){
            Gene a = genes.get(i);
            for(int j=i+1;j<genes.size();j++){
                Gene b = genes.get(j);
                // 同一时间同一老师冲突
                if (a.timeSlot == b.timeSlot && a.teacherId == b.teacherId) conflict++;
                // 同一班级同一时间有多门课冲突
                if (a.classId == b.classId && a.timeSlot == b.timeSlot) conflict++;
            }
        }
        return -conflict;
    }

    private Chromosome crossover(Chromosome p1 , Chromosome p2, Map<Integer,List<Integer>> mp2, List<Classroom> classrooms, int timeCount){
        Random random = new Random();
        List<Gene> genes = new ArrayList<>();
        for(int i=0;i<p1.genes.size();i++){
            Gene src=random.nextBoolean() ? p1.genes.get(i) : p2.genes.get(i);
            Gene g=new Gene();
            g.classId = src.classId;
            g.courseId = src.courseId;
            g.timeSlot = src.timeSlot; // 修正
            g.classroomId=src.classroomId;
            List<Integer> teacherIds = mp2.get(src.courseId);
            if (teacherIds == null || teacherIds.isEmpty()) throw new RuntimeException("No teacher for course " + src.courseId);
            // 修正teacherId合规性
            if (!teacherIds.contains(src.teacherId)) {
                g.teacherId=teacherIds.get(random.nextInt(teacherIds.size()));
            } else {
                g.teacherId=src.teacherId;
            }
            genes.add(g);
        }
        return new Chromosome(genes);
    }

    private void mutate(Chromosome child ,List<Classroom> classrooms, int timeCount,double mutationRate, Map<Integer,List<Integer>> mp2){
        Random random = new Random();
        for(Gene g: child.genes){
            if(random.nextDouble()<mutationRate) {
                List<Integer> teacherIds = mp2.get(g.courseId);
                if (teacherIds == null || teacherIds.isEmpty()) throw new RuntimeException("No teacher for course " + g.courseId);
                g.teacherId=teacherIds.get(random.nextInt(teacherIds.size()));
            }
            if(random.nextDouble()<mutationRate) {
                g.classroomId=classrooms.get(random.nextInt(classrooms.size())).getClassroomId();
            }
            if(random.nextDouble()<mutationRate) {
                g.timeSlot = random.nextInt(timeCount);
            }
        }
    }
}