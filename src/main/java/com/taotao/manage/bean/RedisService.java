package com.taotao.manage.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 封装redis通用service
 * @author dell
 *
 */
@Service
public class RedisService {
	//通过spring注入连接池对象
	@Autowired(required=false)
	private ShardedJedisPool sharedJedisPool;
	//提供一个方法封装通用的模块
	private<T> T execute(Function<T,ShardedJedis> fun) {
		//得到jedis对象
		ShardedJedis shardedJedis=null;
		try {
			shardedJedis = sharedJedisPool.getResource();
			return fun.callback(shardedJedis);
		} finally {
            if (null != shardedJedis) {
                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                shardedJedis.close();
            }
        }
	}
	
	//提供一个set方法,将数剧存入redis缓存数据库中
	public String set(final String key,final String value) {
		return this.execute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.set(key, value);
			}
			
		});
	}
	
	//提供一个get方法s
	public String get(final String key) {
		return this.execute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.get(key);
			}
			
		});
	}
	//设置删除操作
	public Long del(final String key) {
		return this.execute(new Function<Long, ShardedJedis>() {

			@Override
			public Long callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.del(key);
			}
			
		});
	}
	//设置缓存保留时间
	public Long expire(final String key,final Integer second) {
		return this.execute(new Function<Long, ShardedJedis>() {

			@Override
			public Long callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.expire(key, second);
			}
			
		});
	}
	//设置需要时间的set方法
	public String set(final String key,final String value,final Integer second) {
		return this.execute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				String str = e.set(key, value);
				e.expire(key, second);
				return str;
			}
			
		});
	}
}
