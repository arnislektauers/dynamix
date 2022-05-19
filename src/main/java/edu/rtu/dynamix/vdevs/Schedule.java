package edu.rtu.dynamix.vdevs;

import java.util.Arrays;
import java.util.List;

/**
 * A binary heap that is used for scheduling atomic models in the
 * simulation engine.  The atomic model q_index is used to store
 * the position of the model in the heap. A zero value indicate
 * that the model is not in the heap, and the initial value of any
 * model should be q_index = 0.
 */
public class Schedule {

	/**
	 * Definition of an element in the heap
	 */
	protected class HeapElement {
		AtomicVDEVSModel item;

		long priority = VDEVS.TIME_INFINITE;

		void set(HeapElement src) {
			item = src.item;
			priority = src.priority;
		}

		@Override
		public String toString() {
			return (item != null ? item.getName() : "null") + ": " + priority;
		}
	}

	protected HeapElement[] heap;

	protected int size;

	private Simulator simulator;

	/**
	 * Creates a scheduler with the default or specified initial capacity.
	 * @param simulator+
	 */
	public Schedule(Simulator simulator) {
		this(simulator, 100);
	}

	public Schedule(Simulator simulator, int capacity) {
		this.simulator = simulator;
		heap = new HeapElement[capacity];
		for (int i = 0; i < heap.length; i++) {
			heap[i] = new HeapElement();
		}
		heap[0].priority = -1; // This is a sentinel value
	}

	/* (non-Javadoc)
	 * @see edu.rtu.vdevs.ISchedule#minPriority()
	 */
	public long minPriority() { 
		return heap[1].priority; 
	}

	/* (non-Javadoc)
	 * @see edu.rtu.vdevs.ISchedule#getImminent(java.util.List)
	 */
	public void getImminent(List<AtomicVDEVSModel> imminent) {
		getImminent(imminent, 1);
	}

	/* (non-Javadoc)
	 * @see edu.rtu.vdevs.ISchedule#removeImminent()
	 */
	public void removeImminent() {
		if (size == 0) {
			return;
		}
		
		long tN = minPriority();
		while (minPriority() <= tN) {
			removeMinimum();
		}
	}

	public void schedule(AtomicVDEVSModel model, long priority) {		
		SimulationData modelData = simulator.getSimulationData(model);

		if (modelData.queueIndex != 0/* && heap[modelData.queueIndex].item == model*/) { // If the model is in the schedule
			if (priority >= VDEVS.TIME_INFINITE) { // Remove the model if the next event time is infinite
				// Move the item to the top of the heap
				long min_priority = minPriority();
				modelData.queueIndex = percolateUp(modelData.queueIndex, min_priority);
				heap[modelData.queueIndex].priority = min_priority;
				heap[modelData.queueIndex].item = model;

				// Remove it and return
				removeMinimum();
				return;
			} else if (priority < heap[modelData.queueIndex].priority) { // Decrease the time to next event
				modelData.queueIndex = percolateUp(modelData.queueIndex, priority);
			} else if (priority > heap[modelData.queueIndex].priority) { // Increase the time to next event
				modelData.queueIndex = percolateDown(modelData.queueIndex, priority);
			} else { // Don't do anything if the priority is unchanged
				return;
			}

			heap[modelData.queueIndex].priority = priority;
			heap[modelData.queueIndex].item = model;
		} else if (priority < VDEVS.TIME_INFINITE) { // If it is not in the schedule and the next event time is			
			// not at infinity, then add it to the schedule
			// Enlarge the heap to hold the new model
			size++;
			if (size == heap.length) {
				enlarge();
			}

			// Find a slot and put the item into it
			modelData.queueIndex = percolateUp(size, priority);
			heap[modelData.queueIndex].priority = priority;
			heap[modelData.queueIndex].item = model;
		}	

		// Otherwise, the model is not enqueued and its passive, so don't do anything.
	}

	private int percolateUp(int index, double priority) {
		// Position 0 has priority -1 and this method is always called with priority >= 0 and index > 0. 
		int half = index >>> 1;
		while (priority <= heap[half].priority) {
			heap[index].set(heap[half]);
			
			if (heap[index].item == null) {
				System.out.println("aaa");
			}
			 
			simulator.getSimulationData(heap[index].item).queueIndex = index;
			
			index = half;
			half = index >>> 1;
		}
		return index;
	}

	private int percolateDown(int index, double priority) {
		int child;
		for (; index << 1 <= size; index = child) {
			child = index << 1;

			if (child != size && heap[child + 1].priority < heap[child].priority) {
				child++;
			}

			if (heap[child].priority < priority) {
				heap[index].set(heap[child]);
				simulator.getSimulationData(heap[index].item).queueIndex = index;
			} else {
				break;
			}
		}
		return index;
	}

	protected void removeMinimum() {
		// Don't do anything if the heap is already empty
		if (size == 0) {
			return;
		}
		size--;

		// Set index to 0 to indicate that this model is no longer in the schedule
		simulator.getSimulationData(heap[1].item).queueIndex = 0;

		// If the schedule is empty, set the priority of the last element to DBL_MAX		
		if (size == 0) {			
			heap[1].priority = VDEVS.TIME_INFINITE;
			heap[1].item = null;
		} else { // Otherwise fill the hole left by the deleted model				
			int i = percolateDown(1, heap[size + 1].priority);
			heap[i].set(heap[size + 1]);
			simulator.getSimulationData(heap[i].item).queueIndex = i;
			heap[size + 1].item = null;
		}
	}

	protected void getImminent(List<AtomicVDEVSModel> imm, int root) {
		// Stop if the bottom is reached or the next priority is not equal to the minimum
		if (root > size || heap[1].priority < heap[root].priority) {
			return;
		}

		// Put the model into the imminent set
		simulator.getSimulationData(heap[root].item).active = true;
		imm.add(heap[root].item);

		// Look for more imminent models in the left sub-tree
		getImminent(imm, root << 1);

		// Look in the right sub-tree
		getImminent(imm, root << 1 + 1);
	}

	protected void enlarge() {
		HeapElement[] rheap = Arrays.copyOf(heap, heap.length * 2);
		
		for (int i = heap.length; i < rheap.length; i++) {
			rheap[i] = new HeapElement();
		}
		heap = rheap;
	}

	/* (non-Javadoc)
	 * @see edu.rtu.vdevs.ISchedule#getSize()
	 */
	public int getSize() {
		return size;
	}	
}
