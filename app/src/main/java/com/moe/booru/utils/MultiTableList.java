package com.moe.booru.utils;
import java.util.List;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class MultiTableList<T extends Object> implements List<T>
{
	private Filter<T> filter;
	private List<T> sources=new ArrayList<>(),filters=new ArrayList<>();
	@Override
	public <T extends Object> T[] toArray(T[] p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public T get(int p1)
	{
		// TODO: Implement this method
		return filter==null?sources.get(p1):filters.get(p1);
	}

	@Override
	public T set(int p1, T p2)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public void add(int p1, T p2)
	{
		// TODO: Implement this method
	}

	@Override
	public T remove(int p1)
	{
		// TODO: Implement this method
		return null;
	}
	
	public void setFilter(Filter<T> filter){
		this.filter=filter;
		if(filter==null)filters.clear();
		else
		{
			Iterator<T> i=sources.iterator();
			while(i.hasNext()){
				T t=i.next();
				if(filter.accept(t))
					filters.add(t);
			}
		}
	}
	@Override
	public int size()
	{
		// TODO: Implement this method
		return filter==null?sources.size():filters.size();
	}

	@Override
	public boolean isEmpty()
	{
		// TODO: Implement this method
		return filter==null?sources.isEmpty():filters.isEmpty();
	}

	@Override
	public boolean contains(Object p1)
	{
		return sources.contains(p1);
	}

	@Override
	public Iterator iterator()
	{
		// TODO: Implement this method
		return filter==null?sources.iterator():filters.iterator();
	}

	@Override
	public Object[] toArray()
	{
		// TODO: Implement this method
		return filter==null?sources.toArray():filters.toArray();
	}

	@Override
	public boolean add(T p1)
	{
		sources.add(p1);
		if(filter!=null&&filter.accept(p1))filters.add(p1);
		return false;
	}

	@Override
	public boolean remove(Object p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean containsAll(Collection p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean addAll(Collection p1)
	{
		sources.addAll(p1);
		if(filter!=null){
			Iterator<T> i= p1.iterator();
			while(i.hasNext())
			{
				T t=i.next();
				if(filter.accept(t))
					filters.add(t);
			}
		}
		return true;
	}

	@Override
	public boolean addAll(int p1, Collection p2)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean removeAll(Collection p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean retainAll(Collection p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void clear()
	{
		filters.clear();
		sources.clear();
	}

	

	@Override
	public int indexOf(Object p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int lastIndexOf(Object p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public ListIterator listIterator()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public ListIterator listIterator(int p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public List subList(int p1, int p2)
	{
		// TODO: Implement this method
		return null;
	}
	
}
