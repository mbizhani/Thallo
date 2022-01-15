package org.devocative.thallo.hlf.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.devocative.thallo.hlf.HlfClient;
import org.devocative.thallo.hlf.Submit;
import org.devocative.thallo.hlf.iservice.IHlfService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;

public class HlfClientMethodHandler implements InvocationHandler {
	private final Class<?> clientInterfaceClass;
	private final IHlfService hlfService;
	private final ObjectMapper objectMapper;

	// ------------------------------

	public HlfClientMethodHandler(Class<?> clientInterfaceClass, IHlfService hlfService, ObjectMapper objectMapper) {
		this.clientInterfaceClass = clientInterfaceClass;
		this.hlfService = hlfService;
		this.objectMapper = objectMapper;
	}

	// ------------------------------

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		switch (method.getName()) {
			case "hashCode":
				return clientInterfaceClass.hashCode();
			case "toString":
				return clientInterfaceClass.getCanonicalName();
			default:
				return callChaincode(method, args);
		}
	}

	// ------------------------------

	private Object callChaincode(Method method, Object[] args) {
		try {
			final HlfClient hlfClient = clientInterfaceClass.getAnnotation(HlfClient.class);
			final String chaincode = hlfClient.chaincode();

			String[] chaincodeArgs = null;
			if (args != null) {
				chaincodeArgs = new String[args.length];
				for (int i = 0; i < args.length; i++) {
					chaincodeArgs[i] = args[i] != null ? args[i].toString() : "";
				}
			}

			final byte[] result;
			if (method.isAnnotationPresent(Submit.class)) {
				result = hlfService.submit(chaincode, method.getName(), chaincodeArgs);
			} else {
				result = hlfService.evaluate(chaincode, method.getName(), chaincodeArgs);
			}
			return processResult(method, result);
		} catch (Exception e) {
			throw new RuntimeException(String.format("HlfClient: %s::%s(%s)",
				clientInterfaceClass.getCanonicalName(), method.getName(), Arrays.toString(args)), e);
		}
	}

	private Object processResult(Method method, byte[] result) throws Exception {
		final Class<?> returnClass = method.getReturnType();
		if (byte[].class.equals(returnClass)) {
			return result;
		} else if (Void.TYPE.equals(returnClass)) {
			return null;
		} else if (String.class.equals(returnClass)) {
			return new String(result);
		} else if (Boolean.class.equals(returnClass)) {
			return Boolean.valueOf(new String(result));
		} else if (Integer.class.equals(returnClass)) {
			return Integer.valueOf(new String(result));
		} else if (Long.class.equals(returnClass)) {
			return Long.valueOf(new String(result));
		} else if (Float.class.equals(returnClass)) {
			return Float.valueOf(new String(result));
		} else if (Double.class.equals(returnClass)) {
			return Double.valueOf(new String(result));
		} else if (BigDecimal.class.equals(returnClass)) {
			return new BigDecimal(new String(result));
		} else {
			final Type genericReturnType = method.getGenericReturnType();
			final TypeFactory typeFactory = objectMapper.getTypeFactory();
			final JavaType javaType = typeFactory.constructType(genericReturnType);
			return objectMapper.readValue(new String(result), javaType);
		}
	}
}
