package org.devocative.thallo.hlf.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.devocative.thallo.hlf.iservice.IHlfService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
		final byte[] result;
		if (args != null) {
			final String[] chaincodeArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				chaincodeArgs[i] = args[i] != null ? args[i].toString() : "";
			}

			result = hlfService.evaluate(method.getName(), chaincodeArgs);
		} else {
			result = hlfService.evaluate(method.getName());
		}

		return processResult(method, result);
	}

	private Object processResult(Method method, byte[] result) {

		if (method.getGenericReturnType() instanceof ParameterizedType) {
			final ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
			final Class<?>[] params = new Class[type.getActualTypeArguments().length];
			for (int i = 0; i < type.getActualTypeArguments().length; i++) {
				params[i] = load(type.getActualTypeArguments()[i]);
			}
			final TypeFactory typeFactory = objectMapper.getTypeFactory();
			final JavaType javaType = typeFactory.constructParametricType(load(type.getRawType()), params);
			return deserialize(result, javaType);
		} else {
			final Class<?> returnType = method.getReturnType();
			if (byte[].class.equals(returnType)) {
				return result;
			} else if (String.class.equals(returnType)) {
				return new String(result);
			} else {
				return deserialize(result, returnType);
			}
		}
	}

	private Class<?> load(Type type) {
		try {
			return Class.forName(type.getTypeName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Object deserialize(byte[] result, JavaType targetType) {
		try {
			return objectMapper.readValue(new String(result), targetType);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private Object deserialize(byte[] result, Class<?> targetType) {
		try {
			return objectMapper.readValue(new String(result), targetType);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
