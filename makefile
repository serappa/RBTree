# define a makefile variable for the java compiler
JCC = javac
# typing 'make' will invoke the first target entry in the makefile	
# (the default one in this case)
default: all 
all: bbst.java
	$(JCC) $(JFLAGS) bbst.java
# To start over from scratch, type 'make clean'.  
# Removes all .class files, so that the next make rebuilds them
clean: 
	rm *.class
	rm edu/ufl/ads/proj/event/*.class
	rm edu/ufl/ads/proj/rbtree/*.class
