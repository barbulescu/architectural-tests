package com.example.archtests.fixtures.coding.valid;

public class Clean { private final String name; public Clean(String name) { this.name = name; } void fail() { throw new IllegalStateException(name); } }
