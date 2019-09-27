package hu.rxd.toolbox.switcher;

public interface IComponent {
  void switchTo(Version version) throws Exception;

  Component getComponentType();

}