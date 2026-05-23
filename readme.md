# Story Graph Tools

This project provides multiple simple command line tools for generating,
pruning, organizing, and exploring story graphs using the
[Story Graph Library](https://github.com/sgware/story-graph).

A story graph represents all possible states in an interactive narrative. It
defines a story's characters (players and NPCs), some fluents (or variables)
whose values define the current world state, each character's beliefs about the
current world state, actions that can happen and which characters are taking
those actions, and utility values that define how desirable a world state is for
the story's author and for each character. The documentation of the Story Graph
Library has more details on story graphs.

This project provides the following tools for using story graphs:
- `sg-gen`: Generates a story graph from a
[Sabre](https://github.com/sgware/sabre) narrative planning problem.
- `sg-explain`: Add explanations to actions that show how the actions can
improve the story utility and how characters believe the actions can improve
their utilities.
- `sg-rmunx`: Remove unexplained non-player character actions (i.e. actions
taken by NPCs that those characters do not have a reason to take.
- `sg-rmdis`: Remove nodes from the graph which cannot be reached via actions
or beliefs when starting from node 0.
- `sg-rmunu`: Remove characters, fluents, values, actions, states, and plan that
are never used in the story.
- `sg-rmdup`: Remove duplicate state and plan objects and replace duplicate
nodes to save space.
- `sg-sort`: Sort the symbols in a story graph alphabetically and sort nodes
and states by their distance from node 0.
- `sg-clean`: A combination of the `sg-rmdis`, `sg-rmunu`, `sg-rmdup`, and
`sg-sort` tools that removes unused and duplicate elements and sorts the story
graph.
- `sg-explore`: A tool for manually exploring a story graph that starts at node
0, prints a description of the current node, and parses commands to navigate the
graph by choosing which node to visit next.

This project also contains some utilities for creating story graph tools (like
the
[StoryGraphTool](https://sgware.github.io/story-graph-tools/edu/uky/cs/nil/sg/StoryGraphTool.html)
and
[SimpleStoryGraphTool](https://sgware.github.io/story-graph-tools/edu/uky/cs/nil/sg/SimpleStoryGraphTool.html)
classes) and basic data structures with large capacities for dealing with large
story graphs (like
[BigMap](https://sgware.github.io/story-graph-tools/edu/uky/cs/nil/sg/BigMap.html),
[BigSet](https://sgware.github.io/story-graph-tools/edu/uky/cs/nil/sg/BigSet.html), and
[BigQueue](https://sgware.github.io/story-graph-tools/edu/uky/cs/nil/sg/BigQueue.html)).

## Download and Documentation

These tools are written in pure Java and depend on this
[library](https://github.com/sgware/story-graph). You can 
[download the pre-compiled JAR files here](https://github.com/sgware/story-graph-tools/tree/main/build/jar).

The [JavaDoc API is here](https://sgware.github.io/story-graph-tools).

You can download and compile these tools from source using
[Maven](http://maven.apache.org/) like this:
```
git clone https://github.com/sgware/story-graph.git
cd story-graph
mvn clean install
git clone https://github.com/sgware/sabre.git
cd sabre
mvn clean install
git clone https://github.com/sgware/story-graph-tools.git
cd story-graph-tools
mvn clean install
```

You can add these tools to a Maven project's `pom.xml` file like this:
```
<project>
  ...
  <dependencies>
    <!-- Story Graph Tools -->
    <dependency>
      <groupId>edu.uky.cs.nil</groupId>
      <artifactId>story-graph-tools</artifactId>
      <version>1.0.0</version> <!-- use most recent version -->
    </dependency>
  </dependencies>
  ...
</project>
```

## Example Usage

```
# Clone this project.
git clone https://github.com/sgware/story-graph-tools
# Clone some example Sabre problems.
cd story-graph-tools/build/jar
git clone https://github.com/sgware/sabre-benchmarks
# Show the help text for the Story Graph Generator tool.
java -jar sg-gen.jar -h
# Generate the complete story graph for the MacGuffin problem, making Tom the player character.
java -jar sg-gen.jar sabre-benchmarks/problems/macguffin.txt -p Tom -o macguffin.zip
# Add explanations to temporal edges.
java -jar sg-explain.jar macguffin.zip
# Explore the graph.
java -jar sg-explore.jar macguffin.zip
# Remove actions taken by but not explained for non-player characters.
java -jar sg-rmunx.jar macguffin.zip -o macguffin-pruned.zip
# Clean the story graph (equivalent to running sg-rmdis, sg-rmunu, sg-rmdup, sg-sort in order).
java -jar sg-clean.jar macguffin-pruned.zip
# Explore the pruned graph.
java -jar sg-explore.jar macguffin-pruned.zip
```

## Ownership and License

The Story Graph Library and these Story Graph Tools were originally developed by
Stephen G. Ware PhD, Associate Professor of Computer Science at the University
of Kentucky in 2025. Development of this software was sponsored in part by a
grant from the US National Science Foundation, #2145153.

This project is released under the
[General Public License version 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).
In short, this means you are free to download, use, modify, and redistribute
this software as long as you continue to acknowledge the original copyright
holders and as long as you make the software that you create with these tools
freely and publicly available under a similar license.

See the license file for full details. The University of Kentucky retains all
rights not specifically granted.

This license allows you to use this software in commercial projects, but only if
you also release your project under a compatible open source license. If you
want to use this software in a project that is not open source, exceptions can
be granted by the copyright holders. Contact the University of Kentucky Office
of Technology Commercialization at otcinfo@uky.edu to discuss licensing this
software for other kinds of projects.

## Version History

- Version 1.0.0: First public release.

## Citation

Please cite this library like this:

> Stephen G. Ware, "Story Graph Tools," GitHub, 2025.
> https://github.com/sgware/story-graph-tools

BiBTeX entry:

```
@misc{ware2025storygraph,
  author={Ware, Stephen G.},
  title={Story Graph Tools},
  publisher={GitHub},
  year={2025},
  howpublished = {\url{https://github.com/sgware/story-graph-tools}}
}
```