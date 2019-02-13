/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author dell
 * There is only one extended view. You can loop thru the links 
 */
public abstract class AbstractSchemaView implements Comparable {
    
    //the name is the alias
    private String name;
    private SchemaElement element;
    
    //this is impt because we need to arrange them accordingly
    private int joinLevel = 0;
    private int extLevel = 0;
    
    //extend view represents the view for extended. Normally the top 
    //class is the context, which is the parent. see example below:
    //where entityindividual is the context class.
    //parent:  abstractentity->entity->entityindividual 
    //extend: entityindividual->entity->abstractentity
    //use parent if you want to join. Use extend if you want to create
    private SchemaView rootView;
    private AbstractSchemaView parent;
    private AbstractSchemaView extendsView;
    
    private List<AbstractSchemaView> oneToOneViews = new ArrayList();
    private List<AbstractSchemaView> manyToOneViews = new ArrayList();
    
    
    
    public AbstractSchemaView(String name, SchemaElement elem) {
        this.name = name;
        this.element = elem;
    }
    
    public SchemaElement getElement() {
        return element;
    }
    
    public AbstractSchemaView getParent() {
        return parent;
    }

    protected void setParent(AbstractSchemaView p) {
        this.parent = p;
        this.joinLevel = p.getJoinLevel()+1;
    }
    
    public String getName() {
        return name;
    }

    public AbstractSchemaView getExtendsView() {
        return extendsView;
    }

    public void setExtendsView(AbstractSchemaView extendsView) {
        this.extendsView = extendsView;
        this.extLevel = extendsView.getExtLevel()+1;
    }
   
    /***
     * this traverses from the context up to the root object
     * This includes the current context
     */ 
    public List<AbstractSchemaView> getJoinPaths() {
        List<AbstractSchemaView> joinPaths = new ArrayList();
        AbstractSchemaView vw = this;
        joinPaths.add(vw);
        while( (vw=vw.getParent())!=null ) {
            joinPaths.add(vw);
        }
        return joinPaths;
    }
    
    /**
     * This is basically used for displaying the extends in sequence
     * i.e. from the topmost up to the lowest
     */
    public List<AbstractSchemaView> getExtendPaths() {
        Stack<AbstractSchemaView> stack = new Stack();
        AbstractSchemaView vw = this;
        while( (vw=vw.getExtendsView())!=null ) {
            stack.push(vw);
        }
        List<AbstractSchemaView> extendPaths = new ArrayList();
        while(!stack.empty()) {
            extendPaths.add(stack.pop());
        }
        return extendPaths;
    }
    

    public int getJoinLevel() {
        return joinLevel;
    }

    public int getExtLevel() {
        return extLevel;
    }

    public int compareTo(Object o) {
        AbstractSchemaView vw = (AbstractSchemaView)o;
        if( this.getJoinLevel() < vw.getJoinLevel() ) {
            return -1;
        }
        else if(  this.getJoinLevel() > vw.getJoinLevel() ) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }
    
    //True if the main view is joined as an extended element
    public boolean isExtendedView() {
        return false;
    }
    
    public String toString() {
        return this.getName();
    }
    
    public String getPrefix() {
        return null;
    }
    
    //This is based on the number of nested elements. This is simply calculated
    //by the number of underscores separating the prefix 
    public int getNestedCount() {
        if( getPrefix() == null ) return 0;
        String[] arr = getPrefix().split("_");
        return arr.length + 1;
    }
    
    public String getTablename() {
        return element.getTablename();
    }

    public void addOneToOneView( AbstractSchemaView vw ) {
        oneToOneViews.add( vw );
    }
    
    public void addManyToOneView( AbstractSchemaView vw ) {
        manyToOneViews.add( vw );
    }

    public List<AbstractSchemaView> getOneToOneViews() {
        return oneToOneViews;
    }

    public List<AbstractSchemaView> getManyToOneViews() {
        return manyToOneViews;
    }
    
    public SchemaView getRootView() {
        return rootView;
    }

    public void setRootView(SchemaView rootView) {
        this.rootView = rootView;
    }

    
}
