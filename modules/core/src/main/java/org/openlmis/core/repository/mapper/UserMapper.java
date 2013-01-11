package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

  @Select(value = "SELECT userName, id FROM users WHERE LOWER(userName)=LOWER(#{userName}) AND password=#{password}")
  User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

  @Insert(value = {"INSERT INTO users",
      "(userName, password, facilityId, firstName, lastName, employeeId, jobTitle, " +
          "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId) VALUES",
      "(#{userName}, #{password}, #{facilityId},#{firstName},#{lastName},#{employeeId},#{jobTitle}," +
          "#{primaryNotificationMethod},#{officePhone},#{cellPhone},#{email},#{supervisorId})"})
  @Options(useGeneratedKeys = true)
  Integer insert(User user);

  @Select({"SELECT U.* FROM users U INNER JOIN role_assignments RA ON U.id = RA.userId INNER JOIN role_rights RR ON RA.roleId = RR.roleId ",
  "WHERE RA.programId = #{programId} AND RA.supervisoryNodeId = #{supervisoryNodeId} AND RR.rightName = #{right}"})
  List<User> getUsersWithRightInNodeForProgram(@Param("programId")Integer programId, @Param("supervisoryNodeId")Integer supervisoryNodeId, @Param("right")Right right);
}
