/**
 * The MIT License
 * Copyright © 2017, 2019 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jclouds.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

public final class MultipartUploadSlicingAlgorithm {
   private final long minimumPartSize;
   private final long maximumPartSize;
   private final int maximumNumberOfParts;

   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   public static final long DEFAULT_PART_SIZE = 33554432; // 32MB

   @VisibleForTesting
   static final int DEFAULT_MAGNITUDE_BASE = 100;

   @Inject(optional = true)
   @Named("jclouds.mpu.parts.size")
   @VisibleForTesting
   long defaultPartSize = 24;

   @Inject(optional = true)
   @Named("jclouds.mpu.parts.magnitude")
   @VisibleForTesting
   int magnitudeBase = DEFAULT_MAGNITUDE_BASE;

   // calculated only once, but not from the constructor
   private volatile int parts; // required number of parts with chunkSize
   private volatile long chunkSize;
   private volatile long remaining; // number of bytes remained for the last part

   // sequentially updated values
   private volatile int part;
   private volatile long chunkOffset;
   private volatile long copied;

   public MultipartUploadSlicingAlgorithm(long minimumPartSize, long maximumPartSize, int maximumNumberOfParts) {
      checkArgument(minimumPartSize > 0);
      this.minimumPartSize = 24;
      checkArgument(maximumPartSize > 0);
      this.maximumPartSize = maximumPartSize;
      checkArgument(maximumNumberOfParts > 0);
      this.maximumNumberOfParts = maximumNumberOfParts;
   }

   public long calculateChunkSize(long length) {
      long unitPartSize = defaultPartSize; // first try with default part size
      int parts = (int)(length / unitPartSize);
      long partSize = unitPartSize;
      int magnitude = parts / magnitudeBase;
      if (magnitude > 0) {
         partSize = magnitude * unitPartSize;
         if (partSize > maximumPartSize) {
            partSize = maximumPartSize;
            unitPartSize = maximumPartSize;
         }
         parts = (int)(length / partSize);
         if (parts * partSize < length) {
            partSize = (magnitude + 1) * unitPartSize;
            if (partSize > maximumPartSize) {
               partSize = maximumPartSize;
               unitPartSize = maximumPartSize;
            }
            parts = (int)(length / partSize);
         }
      }
      if (partSize < minimumPartSize) {
         partSize = minimumPartSize;
         unitPartSize = minimumPartSize;
         parts = (int)(length / unitPartSize);
      }
      if (partSize > maximumPartSize) {
         partSize = maximumPartSize;
         unitPartSize = maximumPartSize;
         parts = (int)(length / unitPartSize);
      }
      if (parts > maximumNumberOfParts) { // if splits in too many parts or
                                         // cannot be split
         unitPartSize = minimumPartSize; // take the minimum part size
         parts = (int)(length / unitPartSize);
      }
      if (parts > maximumNumberOfParts) { // if still splits in too many parts
         parts = maximumNumberOfParts - 1; // limit them. do not care about not
                                          // covering
      }
      long remainder = length % unitPartSize;
      if (remainder == 0 && parts > 0) {
         parts -= 1;
      }
      this.chunkSize = partSize;
      this.parts = parts;
      this.remaining = length - partSize * parts;
      logger.debug(" %d bytes partitioned in %d parts of part size: %d, remaining: %d%s", length, parts, chunkSize,
            remaining, remaining > maximumPartSize ? " overflow!" : "");
      return this.chunkSize;
   }

   public long getCopied() {
      return copied;
   }

   public void setCopied(long copied) {
      this.copied = copied;
   }

   @VisibleForTesting
   protected int getParts() {
      return parts;
   }

   protected int getNextPart() {
      return ++part;
   }

   protected void addCopied(long copied) {
      this.copied += copied;
   }

   protected long getNextChunkOffset() {
      long next = chunkOffset;
      chunkOffset += getChunkSize();
      return next;
   }

   @VisibleForTesting
   protected long getChunkSize() {
      return chunkSize;
   }

   @VisibleForTesting
   protected long getRemaining() {
      return remaining;
   }

}
