JAVA_FILES = $(wildcard src/*.java)
CLASS_FILES = $(patsubst src/%.java, classes/%.class, $(JAVA_FILES))
JAVAC_FLAG = -cp classes -d classes
JARS = jars/FirstFollowCalculator.jar jars/ParsingTableBuilder.jar jars/ParseTreeReader.jar jars/Parser.jar

.PHONY: all clean

all: classes $(CLASS_FILES) jars $(JARS)

clean:
	rm -rf classes/* jars/*

classes:
	mkdir -p classes

classes/Symbol.class:
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/ProductionRule.class: classes/Symbol.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/Syntax.class: classes/Symbol.class classes/ProductionRule.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/IncrementedSyntax.class: classes/Syntax.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/Core.class: classes/Symbol.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/Item.class: classes/Core.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/ItemSet.class: classes/Item.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/LRSet.class: classes/ItemSet.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/Action.class:
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/ParsingTable.class: classes/Symbol.class classes/ProductionRule.class classes/Action.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/ParsingTableBuilder.class: classes/LRSet.class classes/IncrementedSyntax.class classes/ParsingTable.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/Sentence.class:
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/ParseTree.class: classes/Symbol.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/StackElement.class: classes/Symbol.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

classes/Parser.class: classes/ParsingTable.class classes/Sentence.class classes/ParseTree.class classes/StackElement.class classes/Action.class classes/ProductionRule.class
	javac $(JAVAC_FLAG) $(patsubst classes/%.class, src/%.java, $@)

jars:
	mkdir -p jars

jars/FirstFollowCalculator.jar: classes $(CLASS_FILES)
	cd $(PWD)/classes; jar -c -f ../jars/FirstFollowCalculator.jar -e Syntax *

jars/ParsingTableBuilder.jar: classes $(CLASS_FILES)
	cd $(PWD)/classes; jar -c -f ../jars/ParsingTableBuilder.jar -e ParsingTableBuilder *

jars/ParseTreeReader.jar: classes $(CLASS_FILES)
	cd $(PWD)/classes; jar -c -f ../jars/ParseTreeReader.jar -e ParsingTable *

jars/Parser.jar: classes $(CLASS_FILES)
	cd $(PWD)/classes; jar -c -f ../jars/Parser.jar -e Parser *
