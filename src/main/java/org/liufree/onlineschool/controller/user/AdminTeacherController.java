package org.liufree.onlineschool.controller.user;

import org.liufree.onlineschool.bean.course.Course;
import org.liufree.onlineschool.bean.user.TeacherDto;
import org.liufree.onlineschool.bean.user.User;
import org.liufree.onlineschool.controller.common.Config;
import org.liufree.onlineschool.dao.course.CourseDao;
import org.liufree.onlineschool.dao.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lwx
 * @date 3/15/18
 * @email liufreeo@gmail.com
 */
@Controller
@RequestMapping("/admin")
public class AdminTeacherController {

    @Autowired
    UserDao userDao;
    @Autowired
    CourseDao courseDao;

    @RequestMapping("/teacher/addPage")
    public String addPage() {
        return "admin/teacher_add";
    }

    @PostMapping("/teacher/add")
    public String add(User teacher) {
        teacher.setRole(2);   //设置为老师
        teacher.setCreateTime(new Date());
        userDao.save(teacher);
        return "redirect:/admin/teacher/teacherList";
    }

    @RequestMapping("/teacher/teacherList")
    public String teacherList(Model model, HttpSession session) {

        List<Course> courseList = courseDao.findAll();
        List<User> teacherList = userDao.getListByRole(Config.isTeacher);
        model.addAttribute("courseList", courseList);
        model.addAttribute("teacherList", teacherList);
        List<TeacherDto> list = new ArrayList<>();

        for (User teacher : teacherList) {
            TeacherDto teacherDto = new TeacherDto();
            StringBuilder courses = new StringBuilder();
            for (Course course : courseList) {
                if (course.getTeacher().getId() == teacher.getId()) {
                    courses.append(',').append(course.getTitle());
                }
            }
            if (courses.length() > 1)
                courses = new StringBuilder(courses.substring(1));
            teacherDto.setUsername(teacher.getUsername());
            teacherDto.setCourses(courses.toString());
            teacherDto.setId(teacher.getId());
            list.add(teacherDto);
        }
        model.addAttribute("list", list);

        return "admin/teacher_management";
    }

    @RequestMapping("/teacher/updatePage/{teacherId}")
    public String updatePage(@PathVariable("teacherId") int teacherId, Model model) {
        User teacher = userDao.getOne(teacherId);
        model.addAttribute("teacher", teacher);
        return "admin/teacher_change";
    }

    @PostMapping("/teacher/update/{teacherId}")
    public String update(@PathVariable("teacherId") int teacherId, User teacher, Model model) {
        User user = userDao.getOne(teacherId);
        user.setUsername(teacher.getUsername());
        user.setCountry(teacher.getCountry());
        user.setAddress(teacher.getAddress());
        user.setPassword(teacher.getPassword());
        user.setEmail(teacher.getEmail());
        user.setSex(teacher.getSex());
        user.setMobile(teacher.getMobile());
        user.setSex(teacher.getSex());
        user.setPostalCode(teacher.getPostalCode());
        userDao.save(user);
        return "redirect:/admin/teacher/teacherList";
    }

    @RequestMapping("/teacher/delete/{teacherId}")
    public String delete(@PathVariable("teacherId") int teacherId) {

        List<Course> courseList = courseDao.findByTeacher(teacherId);
       /* for (Course course : courseList) {
            courseDao.delete(course);  //老师的课程也随之删除
        }*/
        User teacher = userDao.getOne(teacherId);
        teacher.setStatus(1);
        userDao.save(teacher);
        return "redirect:/admin/teacher/teacherList";
    }
}
