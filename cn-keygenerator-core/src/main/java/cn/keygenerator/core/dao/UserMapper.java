package cn.keygenerator.core.dao;
import cn.keygenerator.core.entity.User;


public interface UserMapper {
    int deleteByPrimaryKey(String userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User lockSelectByPrimaryKey(String userId);
}