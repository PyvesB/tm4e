package fr.opensagres.language.textmate.grammar.reader;

import java.io.InputStream;

import fr.opensagres.language.textmate.grammar.parser.PlistParser;
import fr.opensagres.language.textmate.types.IRawGrammar;

public class GrammarReader {

	public static IRawGrammar readGrammarSync(String filePath, InputStream in) throws Exception {
		SyncGrammarReader reader = new SyncGrammarReader(in, getGrammarParser(filePath));
		return reader.load();
	}

	private static IGrammarParser getGrammarParser(String filePath) {
		return PlistParser.INSTANCE;
	}
}
