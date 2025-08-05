package com.example.ZhangDT.service.impl.exam;

import com.example.ZhangDT.bean.exam.Classroom;
import com.example.ZhangDT.bean.exam.Exam;
import com.example.ZhangDT.bean.exam.ExamSchedule;
import com.example.ZhangDT.bean.exam.StudentExam;
import com.example.ZhangDT.service.examArrangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExamArrangeServiceimpl implements examArrangeService {

    private static final Logger logger = LoggerFactory.getLogger(ExamArrangeServiceimpl.class);

    // 1. 构建考试到学生的倒排索引
    // 输入：学生考试关联列表
    // 输出：Map<考试ID, 选修该考试的学生集合>
    private Map<Integer,Set<String>> buildExamtoStudents(List<StudentExam> studentExams){
        // 存储：key=考试ID，value=参加该考试的学生ID集合
        Map<Integer,Set<String>> examtoStudents=new HashMap<>();
        // 遍历所有学生-考试关联记录
        for(StudentExam se:studentExams){
            // 为每个考试ID初始化学生集合（若不存在），并添加当前学生ID
            examtoStudents.computeIfAbsent(se.getExamId(),k->new HashSet<>()).add(se.getStudentId());
        }
        logger.info("构建考试-学生映射: {}", examtoStudents);
        return examtoStudents;
    }

    // 2. 构建考试冲突图
    // 输入：考试到学生的映射
    // 输出：Map<考试ID, 与该考试有冲突的其他考试集合>
    private Map<Integer,Set<Integer>> buildConflictGraph(Map<Integer,Set<String>> examToStudents){
        // 存储：key=考试ID，value=与该考试有冲突的其他考试ID集合
        Map<Integer,Set<Integer>> conflictGraph=new HashMap<>();
        // 提取所有考试ID（用于遍历考试对）
        List<Integer> examIds=new ArrayList<>(examToStudents.keySet());

        for(int i=0;i<examIds.size();i++){
            for(int j=i+1;j<examIds.size();j++){
                Integer examA=examIds.get(i);
                Integer examB=examIds.get(j);
                // 获取两门考试的学生集合
                Set<String> sa=examToStudents.get(examA);
                Set<String> sb=examToStudents.get(examB);
                // 计算交集（同时选修两门考试的学生）
                Set<String> intersection=new HashSet<>(sa);
                intersection.retainAll(sb);

                // 如果交集非空，说明两门考试有冲突，在冲突图中添加双向边
                if(!intersection.isEmpty()){
                    conflictGraph.computeIfAbsent(examA,k->new HashSet<>()).add(examB);
                    conflictGraph.computeIfAbsent(examB,k->new HashSet<>()).add(examA);
                }
            }
        }
        logger.info("构建冲突图: {}", conflictGraph);
        return conflictGraph;
    }

    // 3. 贪心图着色算法：为考试分配时间槽
    // 输入：冲突图、所有考试ID列表
    // 输出：Map<考试ID, 分配的时间槽>
    private Map<Integer,Integer> colorGraph(Map<Integer, Set<Integer>> conflictGraph, List<Integer> allExamIds){
        // 存储：key=考试ID，value=分配的时间槽
        Map<Integer,Integer> examToTimeSlot = new HashMap<>();

        // 遍历所有考试，依次分配时间槽
        for(Integer examId:allExamIds){
            // 记录当前考试的冲突考试已占用的时间槽
            Set<Integer> usedColors = new HashSet<>();

            // 若当前考试存在冲突考试
            if (conflictGraph.containsKey(examId)) {
                // 遍历所有冲突考试，收集它们已分配的时间槽
                for (Integer neighbor : conflictGraph.get(examId)) {
                    // 若冲突考试已分配时间槽，记录该时间槽
                    if (examToTimeSlot.containsKey(neighbor)) {
                        usedColors.add(examToTimeSlot.get(neighbor));
                    }
                }
            }
            int color=1;
            while(usedColors.contains(color)){color++;}
            examToTimeSlot.put(examId,color);
            logger.debug("考试{}分配到时间槽{}", examId, color);
        }
        logger.info("考试与时间槽分配结果: {}", examToTimeSlot);
        return examToTimeSlot;
    }

    // 4. 为考试分配教室（确保同一时间槽内教室不重复，且容量足够）
    private Map<Integer,List<Integer>> assignClassrooms(
            List<Exam> exams, Map<Integer, Integer> examToTimeSlot, List<Classroom> classrooms
    )
    {
        // 存储最终的考试-教室映射
        Map<Integer,List<Integer>> examToClassroom=new HashMap<>();
        // 按时间槽分组考试（同一时间槽的考试需分配不同教室）
        Map<Integer,List<Exam>> timeSlotToExam=new HashMap<>();

        for(Exam exam:exams){
            // 获取当前考试的时间槽
            int timeSlot=examToTimeSlot.getOrDefault(exam.getExamId(),1);
            // 将考试添加到对应时间槽的列表中
            timeSlotToExam.computeIfAbsent(timeSlot,k->new ArrayList<>()).add(exam);
        }

        // 遍历每个时间槽，为该时间槽内的所有考试分配教室
        for(Map.Entry<Integer,List<Exam>> entry:timeSlotToExam.entrySet()){
            int timeSlot=entry.getKey();// 当前时间槽
            List<Exam> examsInSlot=entry.getValue(); // 该时间槽内的所有考试
            Set<Integer> classroomUsed = new HashSet<>();  // 记录当前时间槽已用的教室
            logger.info("时间槽{}分配考试{}", timeSlot, examsInSlot);

            // 为该时间槽内的每门考试分配教室
            for(Exam exam:examsInSlot){
                boolean assigned=false;
                int count = exam.getExamCount();
                logger.debug("考试{}需要考生数{}，开始分配教室", exam.getExamId(), count);
                for(Classroom classroom:classrooms){
                    //教室未被当前时间槽的其他考试使用
                    if(!classroomUsed.contains(classroom.getClassroomId())){
                        examToClassroom.computeIfAbsent(exam.getExamId(),k->new ArrayList<>()).add(classroom.getClassroomId());
                        classroomUsed.add(classroom.getClassroomId());
                        count=count-classroom.getCapacity();
                        logger.debug("考试{}分配教室{}，剩余未分配人数{}", exam.getExamId(), classroom.getClassroomId(), Math.max(count,0));
                        if(count<=0){assigned=true;break;}
                    }
                }
                // 若未找到合适教室，抛出异常（说明资源不足）
                if(!assigned){
                    logger.error("没有足够的教室满足考试{} 的容量和时间安排！", exam.getExamId());
                    throw new RuntimeException("没有足够的教室满足考试 " + exam.getExamId() + " 的容量和时间安排！");
                }
            }
        }
        logger.info("考试与教室分配结果: {}", examToClassroom);
        return examToClassroom;
    }

    private List<ExamSchedule> schedule(
            List<Exam> examList,  // 所有考试
            List<StudentExam> studentExamList,  // 学生-考试关联
            List<Classroom> classroomList  // 可用教室
    )
    {
        logger.info("开始排考，考试数:{}，学生考试数:{}，教室数:{}", examList.size(), studentExamList.size(), classroomList.size());
        // 步骤1：构建考试到学生的映射
        Map<Integer, Set<String>> examToStudens=buildExamtoStudents(studentExamList);
        // 步骤2：构建考试冲突图
        Map<Integer,Set<Integer>> conflictGraph=buildConflictGraph(examToStudens);

        // 提取所有考试ID
        List<Integer> allExamIds=new ArrayList<>();
        for(Exam exam:examList) allExamIds.add(exam.getExamId());

        allExamIds.sort((a,b)->{
            int da=conflictGraph.getOrDefault(a,new HashSet<>()).size();
            int db=conflictGraph.getOrDefault(b,new HashSet<>()).size();
            return Integer.compare(db,da);
        });
        logger.info("考试排序结果:{}", allExamIds);

        // 步骤3：分配时间槽
        Map<Integer,Integer> examToTimeSlot=colorGraph(conflictGraph,allExamIds);
        // 步骤4：分配教室
        Map<Integer,List<Integer>> examToClassroom=assignClassrooms(examList,examToTimeSlot,classroomList);

        // 组装排考结果
        List<ExamSchedule> result=new ArrayList<>();
        for(Exam exam:examList){
            ExamSchedule examSchedule=new ExamSchedule();
            examSchedule.setExamId(exam.getExamId());
            int timeSlot=examToTimeSlot.getOrDefault(examSchedule.getExamId(),1);
            examSchedule.setTimeSlot(timeSlot);
            // 设置分配的教室ID
            examSchedule.setClassroomId(examToClassroom.get(examSchedule.getExamId()));
            result.add(examSchedule);
            logger.info("考试{}安排在时间槽{}，教室{}", exam.getExamId(), timeSlot, examToClassroom.get(examSchedule.getExamId()));
        }
        logger.info("排考完成，总排考数:{}", result.size());
        return result;
    }


    public List<ExamSchedule> arrangeExams(List<Exam> exams,
                                           List<StudentExam> studentExams,
                                           List<Classroom> classrooms) {
        logger.info("排考请求：考试{}，学生考试{}，教室{}", exams.size(), studentExams.size(), classrooms.size());
        classrooms.sort((c1,c2)->Integer.compare(c2.getCapacity(),c1.getCapacity()));
        List<ExamSchedule> result = schedule(exams,studentExams,classrooms);
        logger.info("初步排考结果：{}", result);
        return result;
    }
}