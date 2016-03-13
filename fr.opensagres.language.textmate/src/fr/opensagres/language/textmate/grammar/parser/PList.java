package fr.opensagres.language.textmate.grammar.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.opensagres.language.textmate.grammar.parser.PListObject.Raw;
import fr.opensagres.language.textmate.types.IRawGrammar;
import fr.opensagres.language.textmate.types.IRawRepository;
import fr.opensagres.language.textmate.types.IRawRule;

public class PList extends DefaultHandler implements IRawGrammar {

	private final List<String> errors;
	private PListObject currObject;
	private Object result;
	private String text;

	public PList() {
		this.errors = new ArrayList<String>();
		this.currObject = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("dict".equals(localName)) {
			this.currObject = new PListObject(currObject, false);
		} else if ("array".equals(localName)) {
			this.currObject = new PListObject(currObject, true);
		} else if ("key".equals(localName)) {
			if (currObject != null) {
				currObject.setLastKey(null);
			}
		}
		this.text = "";
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		endElement(localName);
		super.endElement(uri, localName, qName);
	}

	private void endElement(String tagName) {
		Object value = null;
		if ("key".equals(tagName)) {
			if (currObject == null || currObject.isValueAsArray()) {
				errors.add("key can only be used inside an open dict element");
				return;
			}
			currObject.setLastKey(text);
			return;
		} else if ("dict".equals(tagName) || "array".equals(tagName)) {
			if (currObject == null) {
				errors.add(tagName + " closing tag found, without opening tag");
				return;
			}
			value = currObject.getValue();
			currObject = currObject.getParent();
		} else if ("string".equals(tagName) || "data".equals(tagName)) {
			value = text;
		} else if ("date".equals(tagName)) {
			// TODO : parse date
		} else if ("integer".equals(tagName)) {
			try {
				value = Integer.parseInt(text);
			} catch (NumberFormatException e) {
				errors.add(text + " is not a integer");
				return;
			}
		} else if ("real".equals(tagName)) {
			try {
				value = Float.parseFloat(text);
			} catch (NumberFormatException e) {
				errors.add(text + " is not a float");
				return;
			}
		} else if ("true".equals(tagName)) {
			value = true;
		} else if ("false".equals(tagName)) {
			value = false;
		} else if ("plist".equals(tagName)) {
			return;
		} else {
			errors.add("Invalid tag name: " + tagName);
			return;
		}
		if (currObject == null) {
			result = value;
		} else if (currObject.isValueAsArray()) {
			currObject.addValue(value);
		} else {
			if (currObject.getLastKey() != null) {
				currObject.addValue(value);
			} else {
				errors.add("Dictionary key missing for value " + value);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		this.text = String.valueOf(ch, start, length);
		super.characters(ch, start, length);
	}

	@Override
	public IRawRepository getRepository() {
		return (IRawRepository) ((Raw) this.result).get("repository");
	}

	@Override
	public String getScopeName() {
		return (String) ((Raw) this.result).get("scopeName");
	}

	@Override
	public IRawRule[] getPatterns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFileTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFirstLineMatch() {
		// TODO Auto-generated method stub
		return null;
	}

}
