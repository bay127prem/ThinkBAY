package sample.kernel.util.dialect;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MySQL8Dialect extends org.hibernate.dialect.MySQL8Dialect{
	public MySQL8Dialect() {
		super();
		registerFunction("weekday", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "DAYOFWEEK(?1)"));
	}
}