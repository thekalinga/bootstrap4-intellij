package in.thekalinga.snippet.util

import in.thekalinga.snippet.util.intellij.BootstrapGenerator
import in.thekalinga.snippet.util.intellij.FontAwesomeGenerator
import in.thekalinga.snippet.util.readme.ReadmeGenerator

class RegeneratePlugin {
    static void main(String[] args) {
        BootstrapGenerator.main()
        FontAwesomeGenerator.main()
        ReadmeGenerator.main()
        PluginXmlGenerator.main()
    }
}
