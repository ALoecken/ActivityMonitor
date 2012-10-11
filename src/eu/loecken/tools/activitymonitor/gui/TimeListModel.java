package eu.loecken.tools.activitymonitor.gui;

import eu.loecken.tools.activitymonitor.TimeSpan;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Andreas@Loecken.eu
 */
public class TimeListModel extends ArrayList<TimeSpan> implements ListModel<TimeSpan> {

  ArrayList<ListDataListener> listeners = new ArrayList<>();
  protected Object source;

  public TimeListModel(Object src) {
    source = src;
  }

  @Override
  public int getSize() {
    return size();
  }

  @Override
  public TimeSpan getElementAt(int index) {
    return get(index);
  }

  @Override
  public void addListDataListener(ListDataListener l) {
    listeners.add(l);
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
    listeners.remove(l);
  }

  void notifyListeners() {
    // no attempt at optimziation
    ListDataEvent le = new ListDataEvent(source,
            ListDataEvent.CONTENTS_CHANGED, 0, getSize());
    for (int i = 0; i < listeners.size(); i++) {
      ((ListDataListener) listeners.get(i)).contentsChanged(le);
    }
  }

  // REMAINDER ARE OVERRIDES JUST TO CALL NOTIFYLISTENERS
  @Override
  public boolean add(TimeSpan o) {
    boolean b = super.add(o);
    if (b) {
      notifyListeners();
    }
    return b;
  }

  @Override
  public void add(int index, TimeSpan element) {
    super.add(index, element);
    notifyListeners();
  }

  @Override
  public boolean addAll(Collection o) {
    boolean b = super.addAll(o);
    if (b) {
      notifyListeners();
    }
    return b;
  }

  @Override
  public void clear() {
    super.clear();
    notifyListeners();
  }

  @Override
  public TimeSpan remove(int i) {
    TimeSpan o = super.remove(i);
    notifyListeners();
    return o;
  }

  @Override
  public boolean remove(Object o) {
    boolean b = super.remove(o);
    if (b) {
      notifyListeners();
    }
    return b;
  }

  @Override
  public TimeSpan set(int index, TimeSpan element) {
    TimeSpan o = super.set(index, element);
    notifyListeners();
    return o;
  }
}
