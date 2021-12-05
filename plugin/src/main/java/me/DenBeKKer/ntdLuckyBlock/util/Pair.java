package me.DenBeKKer.ntdLuckyBlock.util;

import java.util.Map;

public class Pair<A, B> {
	
	private A a;
	private B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A getKey() { return a; }
	public B getValue() { return b; }
	
	public void setKey(A a) { this.a = a; }
	public void setValue(B b) { this.b = b; }
	
	public static <A, B> Pair<A, B> from(Map.Entry<A, B> entry) {
		return new Pair<A, B>(entry.getKey(), entry.getValue());
	}
	
	@Override
	public String toString() {
		
		return a.toString() + "@" + b.toString();
		
	}
	
	@Override
	public boolean equals(Object object) {
		
		if(object instanceof Pair) {
			Pair<?, ?> pair = (Pair<?, ?>) object;
			return pair.getKey().equals(getKey()) && pair.getValue().equals(getValue());
		}
		return false;
		
	}
	
}
