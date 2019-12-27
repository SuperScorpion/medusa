package com.jy.medusa.gaze.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//@Service
public abstract class BaseServiceImplMedusa<T> implements BaseServiceMedusa<T> {

	private static final Logger logger = LoggerFactory.getLogger(BaseServiceImplMedusa.class);

//	@Autowired
	protected Mapper<T> mapper;

//	@Autowired
	protected void initMapper (Mapper<T> mapper) {
		this.mapper = mapper;
	}

	public int selectCount(Object... ps) {
		return mapper.selectCount(ps);
	}

	public List<T> selectAll(Object... ps) {
		return mapper.selectAll(ps);
	}

	public T selectOne(T entity, Object... ps) {
		return mapper.selectOne(entity, ps);
	}

	public List<T> selectByIds(List<Object> ids, Object... ps) {
		return mapper.selectByPrimaryKeyBatch(ids, ps);
	}

	public T selectById(Object id, Object... ps) {
		return mapper.selectByPrimaryKey(id, ps);
	}

	public List<T> selectListBy(T entity, Object... ps) {
		return mapper.select(entity, ps);
	}

	public int saveOrUpdate(T entity) {
		return 0;//TODO
	}

	public int saveSelective(T entity) {
		return mapper.insertSelective(entity);
	}

	public int save(T entity) {
		return mapper.insert(entity);
	}

	public int saveBatch(List<T> obs, Object... ps) {
		return mapper.insertBatch(obs, ps);
	}

	public int update(T entity, Object... ps) {
		return mapper.updateByPrimaryKey(entity, ps);
	}

	public int updateSelective(T entity) {
		return mapper.updateByPrimaryKeySelective(entity);
	}

	public int updateBatch(List<T> obs, Object... ps) {
		return mapper.updateByPrimaryKeyBatch(obs, ps);
	}

	public int deleteById(Object id) {
		return mapper.deleteByPrimaryKey(id);
	}

	public int deleteBatch(List<Object> ids) {
		return mapper.deleteBatch(ids);
	}

	public int deleteBy(T entity) {
		return mapper.delete(entity);
	}

	public List<T> selectByGaze(Object... ps) {
		return mapper.showMedusaGaze(ps);
	}

	/*public JSONObject resultSuccess(Object result, String msg, JSONObject json) {
		json = json == null ? new JSONObject() : json;
		json.put("data", result);
		json.put("result",0);
		json.put("msg", msg);
		return json;
	}

	public JSONObject resultError(Object result, String msg, JSONObject json) {
		json = json == null ? new JSONObject() : json;
		json.put("data", result);
		json.put("result",1);
		json.put("msg", msg);
		return json;
	}*/
}