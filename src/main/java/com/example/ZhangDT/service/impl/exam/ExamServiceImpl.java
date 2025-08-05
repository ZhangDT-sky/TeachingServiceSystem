package com.example.ZhangDT.service.impl.exam;

import com.example.ZhangDT.bean.exam.Classroom;
import com.example.ZhangDT.bean.exam.Exam;
import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.bean.exam.ExamSchedule;
import com.example.ZhangDT.bean.exam.StudentExam;
import com.example.ZhangDT.mapper.*;
import com.example.ZhangDT.service.ExamService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseOfferingMapper courseOfferingMapper;
    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private StudentCourseMapper studentCourseMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ListableBeanFactory listableBeanFactory;
    @Autowired
    private ClassroomMapper classroomMapper;
    @Autowired
    private StudentExamMapper studentExamMapper;

    /**
     * 添加exam，通过courseid将exam的其他属性补全
     * @param exam
     * @return
     */
    @Override
    public Exam addExam(Exam exam) {
        // 自动设置courseName
        Course course = courseMapper.selectById(exam.getCourseId());
        if (course != null) {
            exam.setCourseName(course.getCourseName());
        }
        List<Integer> majorId=courseOfferingMapper.getMajoridByCourseid(course.getCourseId());
        int count1=0,count2=0;
        if (majorId != null) {
            for(Integer majorId1:majorId){
                count1+=majorMapper.countStudentsByMajorId(majorId1);
            }
        }
        if(course.getCourseId()!=null)
            count2=studentCourseMapper.coutStuentsByCourseId(course.getCourseId());

        exam.setExamCount(count1+count2);
        int result=examMapper.insert(exam);
        if(result>0) return exam;
        return null;
    }

    @Override
    public List<Exam> getAllExams() {
        return examMapper.selectList(null);
    }

    @Override
    public Exam getExamById(Integer examId) {
        return examMapper.selectById(examId);
    }

    /**
     * 通过examid查询需要考试的学生
     * @param examIds
     * @return
     */
    @Override
    public List<StudentExam> getExamScheduleByexamid(List<Integer> examIds) {
        List<Integer> courseIds=new ArrayList<>();
        for (Integer examId : examIds) {
            Exam exam=examMapper.selectById(examId);
            courseIds.add(exam.getCourseId());
        }
        List<StudentExam> studentExams=new ArrayList<>();
        for(int i=0;i<courseIds.size();i++){
            Integer courseId=courseIds.get(i);
            Integer examId=examIds.get(i);
            List<String> studentIds=new ArrayList<>();
            List<String>list1=studentCourseMapper.selectByCourseid(courseId);
            List<Integer> majorIds=courseOfferingMapper.getMajoridByCourseid(courseId);
            List<String> list2=new ArrayList<>();
            for(Integer majorId:majorIds){
                list2.addAll(studentMapper.getStudentByMajor(majorId));
            }

            studentIds.addAll(list1);
            studentIds.addAll(list2);

            for(String studentId:studentIds){
                StudentExam studentExam=new StudentExam();
                studentExam.setStudentId(studentId);
                studentExam.setExamId(examId);
                studentExams.add(studentExam);
            }

        }
        return studentExams;
    }

    @Override
    public List<Exam> getlistByexamid(List<Integer> examIds) {
        List<Exam> list=new ArrayList<>();
        for(Integer examId:examIds){
            Exam exam=examMapper.selectById(examId);
            list.add(exam);
        }
        return list;
    }


    /**
     * 组装成最终结果
     * @param result
     * @return
     */
    @Override
    public List<StudentExam> assign(List<ExamSchedule> result) {
        List<StudentExam> studentExams=new ArrayList<>();
        for(ExamSchedule examSchedule:result){
            Integer examId=examSchedule.getExamId();
            Integer timeSlot=examSchedule.getTimeSlot();
            List<Integer> classroomId=examSchedule.getClassroomId();
            Integer courseId=examMapper.selectById(examId).getCourseId();
            Course course=courseMapper.selectById(courseId);
            List<String> list = studentCourseMapper.selectByCourseid(courseId);
            List<String> list1=courseOfferingMapper.selectStudentIdByCourseId(courseId);
            list.addAll(list1);
            int j=0,cnt=0;
            for(int i=0;i<list.size();i++){
                StudentExam studentExamNew=new StudentExam();
                cnt=cnt+1;
                Integer classroomid = classroomId.get(j);
                Classroom classroom= classroomMapper.selectById(classroomid);
                Integer capacity=classroom.getCapacity();
                if(cnt<=capacity){
                    studentExamNew.setExamId(examId);
                    studentExamNew.setStudentId(list.get(i));
                    studentExamNew.setClassroomId(classroomid);
                    studentExamNew.setTimeSlot(timeSlot);
                    studentExamNew.setCourseName(course.getCourseName());
                }
                else{
                    cnt=0;j++;i--;
                }
                studentExams.add(studentExamNew);
            }
        }
        for(StudentExam studentExam:studentExams) {studentExamMapper.insert(studentExam);}
        return studentExams;
    }

    /**
     * 批量删除相关考试安排
     * @param examIds
     */
    @Override
    public void deleteList(List<Integer> examIds) {
        for(Integer examId:examIds){
            studentExamMapper.deleteByexamid(examId);
        }
    }
} 