* Status: accepted
* Deciders: qwazer
* Date: 2021-10-16

## Context and Problem Statement

Need to define ways to configure md2conf tools.

- Q1. Use only CLI properties or configuration files too?
- Q2. Use directory ".md2conf" as place for default configuration files
  or not?

## Considered Options

### Q1. Option 1. Use only CLI properties

### Q1. Option 2. Use external configuration files

### Q2. Option 1. To use directory ".md2conf" as place for default configuration files

### Q1. Option 2. Do not use directory ".md2conf"

## Decision Outcome

- Use CLI properties and external configuration files.
- Use directory ".md2conf" as place for default configuration files.


## Pros and Cons of the Options

### Using only CLI properties

* Good, because of simple implementation.

### Using external configuration files

* Good, because it adds more flexibility for m2conf toolset users.
* Bad, because of more code and more complex tool - need to add a layer
  to merge CLI properties and config files properties.

### Directory ".md2conf" as place for default configuration files

* Good, because it allows keeping configuration in browsable format near
  documentation source.
* Good, because it is extendable approach
* Good, because this approach is widely used to store tool-specific
  config, for example ".git", ".idea", etc

