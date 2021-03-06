/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.common.expression;

public abstract class PathSegment{

  PathSegment child;

  int hash;

  public abstract PathSegment cloneWithNewChild(PathSegment segment);
  public abstract PathSegment clone();

  public static final class ArraySegment extends PathSegment {
    private final int index;

    public ArraySegment(String numberAsText, PathSegment child){
      this(Integer.parseInt(numberAsText), child);
    }

    public ArraySegment(int index, PathSegment child){
      this.child = child;
      this.index = index;
      assert index >=0;
    }

    public ArraySegment(PathSegment child){
      this.child = child;
      this.index = -1;
    }

    public boolean hasIndex(){
      return index != -1;
    }

    public ArraySegment(int index){
      if(index < 0 ) throw new IllegalArgumentException();
      this.index = index;
    }

    public int getIndex(){
      return index;
    }

    public boolean isArray(){
      return true;
    }

    public boolean isNamed(){
      return false;
    }

    @Override
    public ArraySegment getArraySegment() {
      return this;
    }

    @Override
    public String toString() {
      return "ArraySegment [index=" + index + ", getChild()=" + getChild() + "]";
    }

    @Override
    public int segmentHashCode() {
      return index;
    }

    @Override
    public boolean segmentEquals(PathSegment obj) {
      if (this == obj)
        return true;
      else if (obj == null)
        return false;
      else if (obj instanceof ArraySegment)
        return index == ((ArraySegment)obj).getIndex();
      return false;
    }

    @Override
    public PathSegment clone() {
      PathSegment seg = new ArraySegment(index);
      if(child != null) seg.setChild(child.clone());
      return seg;
    }

    public ArraySegment cloneWithNewChild(PathSegment newChild){
      ArraySegment seg = new ArraySegment(index);
      if(child != null){
        seg.setChild(child.cloneWithNewChild(newChild));
      }else{
        seg.setChild(newChild);
      }
      return seg;
    }
  }


  public static final class NameSegment extends PathSegment {
    private final String path;

    public NameSegment(CharSequence n, PathSegment child){
      this.child = child;
      this.path = n.toString();
    }

    public NameSegment(CharSequence n){
      this.path = n.toString();
    }

    public String getPath(){
      return path;
    }

    public boolean isArray(){
      return false;
    }

    public boolean isNamed(){
      return true;
    }

    @Override
    public NameSegment getNameSegment() {
      return this;
    }

    @Override
    public String toString() {
      return "NameSegment [path=" + path + ", getChild()=" + getChild() + "]";
    }

    @Override
    public int segmentHashCode() {
      return ((path == null) ? 0 : path.toLowerCase().hashCode());
    }

    @Override
    public boolean segmentEquals(PathSegment obj) {
      if (this == obj)
        return true;
      else if (obj == null)
        return false;
      else if (getClass() != obj.getClass())
        return false;

      NameSegment other = (NameSegment) obj;
      if (path == null) {
        return other.path == null;
      }
      return path.equalsIgnoreCase(other.path);
    }

    @Override
    public NameSegment clone() {
      NameSegment s = new NameSegment(this.path);
      if(child != null) s.setChild(child.clone());
      return s;
    }

    @Override
    public NameSegment cloneWithNewChild(PathSegment newChild) {
      NameSegment s = new NameSegment(this.path);
      if(child != null){
        s.setChild(child.cloneWithNewChild(newChild));
      }else{
        s.setChild(newChild);
      }
      return s;
    }

  }

  public NameSegment getNameSegment(){
    throw new UnsupportedOperationException();
  }

  public ArraySegment getArraySegment(){
    throw new UnsupportedOperationException();
  }

  public abstract boolean isArray();
  public abstract boolean isNamed();

  public boolean isLastPath(){
    return child == null;
  }

  public PathSegment getChild() {
    return child;
  }

  void setChild(PathSegment child) {
    this.child = child;
  }

  protected abstract int segmentHashCode();
  protected abstract boolean segmentEquals(PathSegment other);

  @Override
  public int hashCode() {
    int h = hash;
    if (h == 0) {
      h = segmentHashCode();
      h = 31*h + ((child == null) ? 0 : child.hashCode());
      hash = h;
    }
    return h;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    PathSegment other = (PathSegment) obj;
    if (!segmentEquals(other)) {
      return false;
    } else if (child == null) {
      return (other.child == null);
    } else return child.equals(other.child);
  }

}
