# Android Security Patch Oneliner
#
# Copyright (C) 2016 halogenOS
#
# -- TO BE LICENSED --
#
# Until a license has been chosen, following rules apply:
# - Any public changes require the source code to be fully available publicly
# - You are not allowed to use this for malicious purposes
# - THIS PRODUCT AS WELL AS THE SOURCE COMES WITH ABSOLUTELY NO WARRANTY!
# - We are not liable for anything! It's your responsibility what you do!
# - Please do not kang.
#

SHELL := /bin/bash

PROJECT_NAME := Android Security Patch Oneliner
PROJECT_SHORTNAME := aspo
OUT := out
INTERMEDIATES := $(OUT)/intermediates
JAR := $(OUT)/jar/$(PROJECT_SHORTNAME).jar
JAR_INTERMEDIATES := $(OUT)/jar_intermediates
JAVA_SRC_DIR := src/
JAVA_SRC_DIR_F_REGEXP := $(subst /,\/,$(JAVA_SRC_DIR))
JAVA_SOURCE_FILES := $(shell find $(JAVA_SRC_DIR) -name '*.java' -type f)
MAIN_CLASS := org.halogenos.android.secpatch.oneliner.Main

all:
	@echo "Compiling java source files..."
	javac $(JAVA_SOURCE_FILES)
	@echo "Moving intermediates..."
	@while read class; do \
          classwos="$${class/$(JAVA_SRC_DIR_F_REGEXP)/}"; \
          mkdir -p "$$(dirname $(INTERMEDIATES)/java/$$classwos)"; \
          mv "$$class" "$(INTERMEDIATES)/java/$$classwos"; \
        done < <(find src/ -name '*.class' -type f)
	@echo "Preparing for package..."
	@rm -rf $(JAR_INTERMEDIATES)
	@rm -rf $(JAR)
	@mkdir -p $(JAR_INTERMEDIATES)/META-INF
	@mkdir -p $$(dirname $(JAR))
	@while read line; do \
          echo "$${line/MAIN_CLASS_INSERT_HERE/$(MAIN_CLASS)}">>"$(JAR_INTERMEDIATES)/META-INF/MANIFEST.MF"; \
        done < src/MANIFEST.MF
	@echo "Making final package..."
	@cp -R "$(INTERMEDIATES)/java/./" "$(JAR_INTERMEDIATES)/"
	@cd "$(JAR_INTERMEDIATES)" && \
          zip -r9 "$(abspath $(JAR))" "./"
	@echo "Install JAR: $(JAR)"
	@echo "Completed."
	@echo

clean:
	@rm -rf out/ build/

.PHONY: all
