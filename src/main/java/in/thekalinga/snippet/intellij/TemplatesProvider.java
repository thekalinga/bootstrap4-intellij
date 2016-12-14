package in.thekalinga.snippet.intellij;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

public class TemplatesProvider implements DefaultLiveTemplatesProvider {
  public String[] getDefaultLiveTemplateFiles() {
    return new String[] {"bootstrap", "font-awesome"};
  }

  @Nullable
  public String[] getHiddenLiveTemplateFiles() {
    return null;
  }
}
