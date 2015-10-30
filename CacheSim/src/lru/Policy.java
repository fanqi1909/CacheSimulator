package lru;

public interface Policy {
	public int getNextIndex();
	public void updateAt(int index);
}
