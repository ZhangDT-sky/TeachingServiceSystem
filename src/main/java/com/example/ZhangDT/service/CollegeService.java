package com.example.ZhangDT.service;


import com.example.ZhangDT.bean.College;

import java.util.List;

public interface CollegeService {

    List getAllcollege();

    College getCollegebyid(Integer id);

    College add(College college);

    College delete(Integer id);

    College update(College college);
}
