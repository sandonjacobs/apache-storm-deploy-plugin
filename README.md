# apache-storm-deploy-plugin

## The Purpose

The point here is to provide a predictable and (relatively) painless way to manage Apache Storm topologies running in any cluster.

## The Motivation

If you're like my company, you have some complex snarl of a script - written in the your lingua franca - to manage the 
lifecycle of Apache Storm topologies. To go a step further, I bet this process is a snowflake in each storm 
environment/cluster in the enterprise. [Here's an example that works](docs/unproud.md), but I am very "un-proud" of...

Seems harmless, right?!? Especially with this in a bash textarea of 2 different jenkins jobs on 2 
different networks, right!?! With no version control... What could go wrong?

## The Way Forward

## Create an Installation
An "Installation" of Storm in our world is used to one day support multiple versions of Apache Storm from this plugin. 
A conscience effort was made to avoid using storm as a java dependency for this project - that comes with a host of problems
and bloat of it's own. Storm provides a number of API endpoints to get topology status, kill topologies, etc... Let's use those 
instead and in the future we can add support for these as they change.

## Define a Cluster

