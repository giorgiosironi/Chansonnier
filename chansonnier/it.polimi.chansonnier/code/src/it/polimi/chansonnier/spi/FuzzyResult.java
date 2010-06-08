package it.polimi.chansonnier.spi;

public final class FuzzyResult {
	private String _value;
	private Double _confidence;
	
	public FuzzyResult(String value, Double confidence) {
		_value = value;
		_confidence = confidence;
	}

	public String getValue() {
		return _value;
	}
	
	public Double getConfidence() {
		return _confidence;
	}
	
	public String toString() {
		return getValue() + " [" + getConfidence() + "]";
	}
}
