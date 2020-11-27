package marchsoft.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import marchsoft.config.MybatisRedisCache;
import marchsoft.modules.system.entity.Job;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * <p>
 * 岗位 Mapper 接口
 * </p>
 *
 * @author Wangmingcan
 * @since 2020-08-17
 */
@CacheNamespace(implementation = MybatisRedisCache.class, eviction = MybatisRedisCache.class)
public interface JobMapper extends BaseMapper<Job> {


    /**
     * @param id 用户id
     * @return Set<Job>
     * @author Wangmingcan
     * @date 2020-08-23 15:44
     * description 通过用户id和关联表users_jobs查询该用户的所有工作。
     */
    @Select("SELECT j.* FROM sys_job j, sys_users_jobs uj WHERE j.job_id = uj.job_id AND uj.user_id = ${id}")
    @Result(column = "job_id", property = "id")
    Set<Job> findByUserId(Long id);
}
