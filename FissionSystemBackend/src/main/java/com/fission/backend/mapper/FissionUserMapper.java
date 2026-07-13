package com.fission.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fission.backend.entity.FissionUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FissionUserMapper extends BaseMapper<FissionUser> {
}
