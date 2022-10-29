package it.plugandcree.placeholderchat.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdvancedFormat {
	private String condition;
	private String recipentCondition;
	private int priority;
	private String chatFormat;
	private String userHoverText;
}
