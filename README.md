在idea上运行，tomcat版本为Tomcat 10.1.42
使用root用户登录，密码设为123，可在UserDao中修改为自己使用的用户和密码
新建数据库sgs，并在查询中输入以下SQL语句建立数据库：
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_no` varchar(50) NOT NULL COMMENT '工号/学号',
  `real_name` varchar(50) NOT NULL COMMENT '姓名',
  `user_group` enum('教师','学生','管理员') NOT NULL DEFAULT '学生' COMMENT '用户组',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `password` varchar(100) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`user_no`) COMMENT '工号学号唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
