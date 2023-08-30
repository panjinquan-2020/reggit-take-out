package com.mytest.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mytest.reggie.entity.Category;
import org.springframework.stereotype.Service;

/**
 * @author PJQ
 */
@Service
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
