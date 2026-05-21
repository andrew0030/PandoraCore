package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

public interface IPacoInitCachable<T> {
	T pandoraCore$getInitializer();
	
	T pandoraCore$getCurrentInitializer();
	
	void pandoraCore$setInitializer(T initializer);
}
