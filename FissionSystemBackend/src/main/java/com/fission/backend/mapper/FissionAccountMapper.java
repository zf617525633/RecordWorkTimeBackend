package com.fission.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fission.backend.entity.FissionAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FissionAccountMapper extends BaseMapper<FissionAccount> {
}
