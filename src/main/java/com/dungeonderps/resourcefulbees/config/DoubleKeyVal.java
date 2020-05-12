package com.dungeonderps.resourcefulbees.config;

import java.util.List;

public class DoubleKeyVal<K, T, E> {
	private K child;
	private T parent1;
	private E parent2;
	public DoubleKeyVal() {}
	
	public DoubleKeyVal(K child) {
		this.child = child;
	}
	
	public DoubleKeyVal(K child, T parent1) {
		this.child = child;
		this.parent1 = parent1;
	}
	
	public DoubleKeyVal(K child, T parent1, E parent2) {
		this.child = child;
		this.parent1 = parent1;
		this.parent2 = parent2;
	}

	public K getChild() {
		return child;
	}

	public void setChild(K child) {
		this.child = child;
	}

	public T getParent1() {
		return parent1;
	}

	public void setParent1(T parent1) {
		this.parent1 = parent1;
	}

	public E getParent2() {
		return parent2;
	}

	public void setParent2(E parent2) {
		this.parent2 = parent2;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean equals(DoubleKeyVal<K, T, E> x) {
		if (child.equals(x.getChild()) && parent1.equals(x.getParent1()) && parent2.equals(x.getParent2()))
			return true;
		if (child.equals(x.getChild()) && parent1.equals(x.getParent2()) && parent2.equals(x.getParent1()))
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public void put(List<E> x) {
		while (!x.isEmpty()) {
			E curr = x.remove(0);
			if (child == null)
				child = (K) curr;
			else if (parent1 == null)
				parent1 = (T) curr;
			else if (parent2 == null)
				parent2 = (E) curr;
		}
	}
}
