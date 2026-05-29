package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetBreedSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 宠物养成空间Mapper
 */
@Mapper
public interface PetBreedSpaceMapper extends BaseMapper<PetBreedSpace> {

    /**
     * 根据用户ID和宠物ID查询养成空间
     */
    @Select("SELECT * FROM pet_breed_space WHERE user_id = #{userId} AND pet_id = #{petId}")
    PetBreedSpace findByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);
}
