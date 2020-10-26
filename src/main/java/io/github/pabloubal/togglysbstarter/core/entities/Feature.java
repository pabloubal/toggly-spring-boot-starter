package io.github.pabloubal.togglysbstarter.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feature {
  private String name;
  private boolean enabled;
}
