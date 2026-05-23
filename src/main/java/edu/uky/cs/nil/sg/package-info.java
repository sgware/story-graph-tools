/**
 * A collection of command line tools for generating, modifying, and exploring
 * {@link StoryGraph story graphs}.
 * <p>
 * The tools included can be used to:
 * <ul>
 * <li>{@link Generate Generate} a story graph from a Sabre narrative planning
 * problem</li>
 * <li>{@link Explain Find explanations} for the temporal edges in a story
 * graph that improve the author and character utilities</li>
 * <li>{@link RemoveUnexplained Remove unexplained} temporal edges</li>
 * <li>{@link RemoveDisconnected Remove disconnected nodes} that cannot be
 * reached from node 0</li>
 * <li>{@link RemoveUnused Remove unused} story graph elements</li>
 * <li>{@link RemoveDuplicates Remove duplicate} story graph elements</li>
 * <li>{@link Sort Sort} story graph elements</li>
 * <li>{@link Explore Explore} a story graph from the console</li>
 * </ul>
 * <p>
 * This package also contains utility classes for creating story graph tools,
 * like {@link StoryGraphTool} and {@link SimpleStoryGraphTool} for creating
 * tool executables and a handful of useful data structures like {@link BigMap
 * maps}, {@link BigSet sets}, and {@link BigQueue queues} for holding large
 * collections of story graph elements.
 * 
 * @author Stephen G. Ware
 */
package edu.uky.cs.nil.sg;