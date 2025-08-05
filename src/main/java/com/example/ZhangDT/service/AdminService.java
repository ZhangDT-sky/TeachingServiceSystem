package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Admin;

import java.util.List;

public interface AdminService {

    Admin getAdminbyid(Integer adminId);

    List<Admin> getAllAdmin();

    Admin add(Admin admin);

    void delete(Integer adminId);

    Admin update(Admin admin);
}
