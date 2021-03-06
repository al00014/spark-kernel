/*
 * Copyright 2014 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.utils

import akka.actor.Actor

/**
 * A stack which will block when popping items.
 */
class BlockingStack {
  private val items = new scala.collection.mutable.Stack[Any]

  /**
   * Puts an item onto the stack
   * @param item The item to put on the stack
   */
  def put(item : Any) {
    items.synchronized{
      items push item
    }
  }

  /**
   * Tries to pop an item off the stack, will sleep if nothing is there.
   * Will block for 1 second and return an Option.empty if
   * nothing makes it onto the stack.
   * @return The item on the top of the stack or Option.empty
   */
  def pop() : Any = {
    popN(10)
  }

  private def popN(count: Int) : Any = {
    if(count == 0)
      Option.empty
    else if( items.synchronized{ items.size == 0 }) {
      Thread.sleep(100)
      popN(count - 1)
    } else
      items.synchronized {
        items.pop()
      }
    }
}

/**
 * An actor that will push anything it receives onto a BlockingStack
 * @param stack The stack to push items onto
 */
class StackActor(stack : BlockingStack) extends Actor {
  override def receive = {
    case m =>
      stack.put(m)
  }
}