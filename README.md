# compiler assignment

You can refer report.pdf(written in Korean) to learn how this program has designed and additional details.
I use Makefile to manage compile process, so only you need to compile this is just typing `make` into terminal.
After you compile, there will be 4 executable jar files in folder `jars`.

## usage

`java -jar jars/FitstFollowCalculator.jar <syntax file>`

This program calculates first, and follow for each symbol in syntax file.

`java -jar jars/ParsingTableBuilder.jar <SLR|CLR|LALR> <syntax file> [table file] [DEBUG]`

This program calculates parsing table from given syntax file and print it.
LALR method is not implemented for now.
It stores parsing table into file if table file name is given
It prints LR items if DEBUG is given.

`java -jar jars/ParseTreeReader.jar <table file>`

This program prints table file

`java -jar jars/Parser.jar <table file> <sentence file> [DEBUG]`

This program analyzes sentence file using given table file.
It also prints stack, buffer information for each step if DEBUG is given.
