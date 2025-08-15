package us.bringardner.io.filesource.viewer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import us.bringardner.io.filesource.FileSource;

public class FileSourceProxy implements InvocationHandler {

	private FileSource target;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		    Object result = method.invoke(target, args);
   	        return result;
		
	}

}
