package in.thekalinga.snippet.util.readme

class Component {
    String text, code, context

    String getText() {
        text ?: 'No description'
    }
}
