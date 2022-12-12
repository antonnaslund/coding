all:
	javac *.java -Xdiags:verbose

test: all
	java -ea Driver

clean:
	rm -f *.class

memtest:
	@echo "No leaks are possible! (since we are running Java)"

.PHONY: all test class memtest
