package sample.kernel.util.dialect;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MySQL5Dialect extends org.hibernate.dialect.MySQL5Dialect{	
	public MySQL5Dialect() {
		super();
		registerFunction("weekday", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "DAYOFWEEK(?1)"));
	}
}