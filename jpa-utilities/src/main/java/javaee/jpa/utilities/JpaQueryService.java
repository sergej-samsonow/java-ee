package javaee.jpa.utilities;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class JpaQueryService {
	
	@Resource
	private SessionContext context;
	
	@PersistenceContext
	private EntityManager enitityManager;
	
	@Inject
	private NamedQueryWrapperErrorService errorService;
	
	public <T> NamedQueryWrapper<T> named(String queryName, Class<T> resultClass) {
		return new NamedQueryWrapper<>(enitityManager, errorService, queryName, resultClass);
	}
	

}
