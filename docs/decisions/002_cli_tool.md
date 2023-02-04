* Status: accepted
* Deciders: qwazer
* Date: 2021-10-16

## Context and Problem Statement

Need to define ways to invoke and configure md2conf toolset.

## Considered Options

* Use only CLI properties
* Use only external configuration files
* Use both


## Decision Outcome

- Use CLI properties


## Pros and Cons of the Options

### Using only CLI properties

* Good, because of simple implementation.

### Using external configuration files

* Good, because it adds more flexibility for m2conf toolset users.
* Bad, because of more code and more complex tool - need to add a layer
  to merge CLI properties and config files properties.

