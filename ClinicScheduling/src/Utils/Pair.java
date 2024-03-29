package Utils;

/**
 * Create Date: 3/15/17
 * By nicjohnson
 * Class: Pair
 * Description:
 */
public class Pair<A, B>
{
	private final A first;
	private final B second;

	public Pair(A first, B second)
	{
		this.first = first;
		this.second = second;
	}

	public A getFirst()
	{
		return this.first;
	}

	public B getSecond()
	{
		return this.second;
	}
}
