package com.le.conversion.common.functions;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;

public class MultiplePredicates<T> implements Predicate<T> {

	private List<Predicate<T>> predicates;
	private BitSet unMatchedPredicates;

	public MultiplePredicates() {
	}

	public MultiplePredicates(List<Predicate<T>> predicates) {
		// TODO Auto-generated constructor stub
		this.predicates = predicates;
		this.unMatchedPredicates = new BitSet(this.predicates.size());
		this.unMatchedPredicates.set(0, predicates.size(), true);
	}

	public void addPredicates(List<Predicate<T>> predicates) {

		if (this.predicates == null)
			this.predicates = new ArrayList<>();

		this.predicates.addAll(predicates);
	}

	public void addPredicate(Predicate<T> predicate) {

		if (this.predicates == null)
			this.predicates = new ArrayList<>();

		this.predicates.add(predicate);
	}

	public void done() {
		this.unMatchedPredicates = new BitSet(this.predicates.size());
		this.unMatchedPredicates.set(0, this.predicates.size(), true);
	}

	@Override
	public boolean test(T t) {
		// TODO Auto-generated method stub

		unMatchedPredicates.stream().filter(i -> predicates.get(i).test(t)).findFirst()
				.ifPresent(unMatchedPredicates::clear);

		return unMatchedPredicates.isEmpty();
	}

}
