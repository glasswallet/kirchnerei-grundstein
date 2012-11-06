package kirchnerei.grundstein.webapp;

import javax.servlet.http.HttpServletRequest;

public interface ETagStrategy {

	String build(HttpServletRequest request);
}
