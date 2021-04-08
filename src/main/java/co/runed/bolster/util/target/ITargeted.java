package co.runed.bolster.util.target;

public interface ITargeted<T>
{
    Target<T> getTarget();

    void setTarget(Target<T> target);
}
