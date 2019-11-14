package io.noties.markwon.inlineparser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.internal.Bracket;
import org.commonmark.internal.Delimiter;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @see AutolinkInlineProcessor
 * @see BackslashInlineProcessor
 * @see BackticksInlineProcessor
 * @see BangInlineProcessor
 * @see CloseBracketInlineProcessor
 * @see EntityInlineProcessor
 * @see HtmlInlineProcessor
 * @see NewLineInlineProcessor
 * @see OpenBracketInlineProcessor
 * @see MarkwonInlineParser.FactoryBuilder#addInlineProcessor(InlineProcessor)
 * @see MarkwonInlineParser.FactoryBuilder#excludeInlineProcessor(Class)
 * @since 4.2.0-SNAPSHOT
 */
public abstract class InlineProcessor {

    /**
     * Special character that triggers parsing attempt
     */
    public abstract char specialCharacter();

    /**
     * @return boolean indicating if parsing succeeded
     */
    protected abstract boolean parse();


    protected MarkwonInlineParserContext context;
    protected Node block;
    protected String input;
    protected int index;

    public boolean parse(@NonNull MarkwonInlineParserContext context) {
        this.context = context;
        this.block = context.block();
        this.input = context.input();
        this.index = context.index();

        final boolean result = parse();

        // synchronize index
        context.setIndex(index);

        return result;
    }

    protected Bracket lastBracket() {
        return context.lastBracket();
    }

    protected Delimiter lastDelimiter() {
        return context.lastDelimiter();
    }

    @NonNull
    protected Map<String, Link> referenceMap() {
        return context.referenceMap();
    }

    protected void addBracket(Bracket bracket) {
        context.addBracket(bracket);
    }

    protected void removeLastBracket() {
        context.removeLastBracket();
    }

    protected void spnl() {
        context.setIndex(index);
        context.spnl();
        index = context.index();
    }

    @Nullable
    protected String match(@NonNull Pattern re) {
        // before trying to match, we must notify context about our index (which we store additionally here)
        context.setIndex(index);

        final String result = context.match(re);

        // after match we must reflect index change here
        this.index = context.index();

        return result;
    }

    @Nullable
    protected String parseLinkDestination() {
        context.setIndex(index);
        final String result = context.parseLinkDestination();
        this.index = context.index();
        return result;
    }

    @Nullable
    protected String parseLinkTitle() {
        context.setIndex(index);
        final String result = context.parseLinkTitle();
        this.index = context.index();
        return result;
    }

    protected int parseLinkLabel() {
        context.setIndex(index);
        final int result = context.parseLinkLabel();
        this.index = context.index();
        return result;
    }

    protected void processDelimiters(Delimiter stackBottom) {
        context.setIndex(index);
        context.processDelimiters(stackBottom);
        this.index = context.index();
    }

    protected void appendNode(@NonNull Node node) {
        context.appendNode(node);
    }

    @NonNull
    protected Text appendText(@NonNull CharSequence text, int beginIndex, int endIndex) {
        return context.appendText(text, beginIndex, endIndex);
    }

    @NonNull
    protected Text appendText(@NonNull CharSequence text) {
        return context.appendText(text);
    }

    protected char peek() {
        context.setIndex(index);
        return context.peek();
    }
}
